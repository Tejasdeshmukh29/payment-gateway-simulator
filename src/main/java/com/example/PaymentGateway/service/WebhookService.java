package com.example.PaymentGateway.service;


import com.example.PaymentGateway.dto.WebhookPayloadDTO;
import com.example.PaymentGateway.enums.WebhookStatus;
import com.example.PaymentGateway.model.Payment;
import com.example.PaymentGateway.model.WebhookLogs;
import com.example.PaymentGateway.repository.WebhookRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class WebhookService {

    private final static String SECRET = "my-webhook-secret-key-123";
    private ObjectMapper mapper = new ObjectMapper();

    private WebhookRepository webhookRepository;

    private RestTemplate restTemplate = new RestTemplate();
    private String webhookUrl = "http://localhost:8080/merchant/webhook";

    public WebhookService(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    public void sendWebhook(WebhookPayloadDTO payload, Payment payment) {
        WebhookLogs webhookLogs = new WebhookLogs();
        webhookLogs.setWebhookUrl(webhookUrl);
        webhookLogs.setPayment(payment);
        webhookLogs.setCreatedAt(LocalDateTime.now());
        webhookLogs.setLastAttemptAt(LocalDateTime.now());
        webhookLogs.setAttemptCount(1);

        try {

            //convert java object to json
            String payloadJson = mapper.writeValueAsString(payload);
            System.out.println(payloadJson);

            // creates http header
            HttpHeaders headers = new HttpHeaders();

            //creates random hash
            String signature = generateSignature(payloadJson);
            System.out.println(signature);

            // add custome header in HTTP headers with name X-Signature : signature
            headers.set("X-Signature", signature);

            // tells reciver that content is json type
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HttpEntity represent complete http request it contain body = payload json and header so it looks like
            //Headers:
            //Content-Type: application/json
            //X-Signature: abc123
            //Body:
            //{payment JSON}
            HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);

            // this send actual POST request
            restTemplate.postForObject(webhookUrl, entity, String.class);

            webhookLogs.setStatus(WebhookStatus.SUCCESS);
        } catch (Exception e) {
            webhookLogs.setStatus(WebhookStatus.FAILED);
            System.out.println(" Webhook Failed " + e.getMessage());
            e.printStackTrace();
        } finally {
            webhookRepository.save(webhookLogs);
        }
    }

    @Scheduled(fixedRate = 60000)
    //  check after every 1 min and send all failed webhooks ( at max 3 times 1st + 2 times more)
    private void retryFailedWebhooks() {
        List<WebhookLogs> failed = webhookRepository.findByStatus(WebhookStatus.FAILED);//getting all row from DB whrere WebhookStatus = Failed
        for (WebhookLogs log : failed) {
            if (log.getAttemptCount() >= 3) continue;
            log.setAttemptCount(log.getAttemptCount() + 1);
            log.setLastAttemptAt(LocalDateTime.now());
            sendWebhookAgain(log);
        }
    }


    // function for sending webhook again
    private void sendWebhookAgain(WebhookLogs log) {

        WebhookPayloadDTO payload =
                new WebhookPayloadDTO(
                        log.getPayment().getId(),
                        log.getPayment().getAmount(),
                        log.getPayment().getStatus().toString(),
                        log.getPayment().getCurrency()
                );

        try {

            String payloadJson = mapper.writeValueAsString(payload);
            String signature = generateSignature(payloadJson);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Signature", signature);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);

            restTemplate.postForObject(log.getWebhookUrl(), entity, String.class);

            log.setStatus(WebhookStatus.SUCCESS);

        } catch (Exception e) {

            System.out.println("Webhook Failed " + e.getMessage());

        } finally {

            webhookRepository.save(log);

        }
    }


    // adding signature to payload and creating final hmac
    public String generateSignature(String payload) {
        try {
            // MAC = Message Authentication code ,  getInstance("HmacSHA256") = tells java to create Hmac engine with SHA256 algorithm Internally Java loads the cryptographic provider that supports this algorithm,After this line you now have a MAC object capable of computing HMAC-SHA256.
            Mac mac = Mac.getInstance("HmacSHA256");

            //cryptographic algorithms oprates on binary data  , Use these bytes as the secret key, for the HmacSHA256 algorithm
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");

            // intialize mac with secret key , now mac knows which secreate key to use
            mac.init(key);

            //doFinal performs actual HMac computation payload bytes are used as a input  , first convert payload in bytes and then use HMAC_SHA256(secret_key, payload)
            byte[] rawHmac = mac.doFinal(payload.getBytes());

            // Base64 is encoding scheme Purpose:
            //convert binary data → printable string
            //Because HTTP headers cannot safely contain raw binary.
            //Example
            //Raw HMAC bytes:[84,34,122,11,90]
            //Encoded Base64 string : VCK6C1o=

            //Now this string can be safely sent in:
            //HTTP header
            //JSON
            //Logs

            //Base64.getEncoder()
            //Returns a Base64 encoder object.
            //Then:
            //encodeToString(byte[])
            //Converts binary data → encoded String.
            return Base64.getEncoder().encodeToString(rawHmac);

        } catch (Exception e) {
            throw new RuntimeException("failed to genrate Hmac");
        }
    }


}
