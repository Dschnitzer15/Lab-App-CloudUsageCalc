package Calculator;

import CustomerData.Customer;
import CustomerData.CustomerUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/usage")
public class UsageCalculator {

    private final UsageService usageService;

    @Autowired
    public UsageCalculator(UsageService usageService) {
        this.usageService = usageService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<List<CustomerUsage>> calculateUsage(@RequestBody List<Customer> customers) {
        List<CustomerUsage> result = usageService.aggregateUsage(customers);
        sendToReferenceSystem(result);
        return ResponseEntity.ok(result);
    }

    private void sendToReferenceSystem(List<CustomerUsage> usageData) {
        String referenceSystemUrl = "http://localhost:8080/v1/result";

        RestTemplate restTemplate = new RestTemplate();
        ResultPayload payload = createResultPayload(usageData);

        ResponseEntity<Void> response = restTemplate.postForEntity(referenceSystemUrl, payload, Void.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Daten erfolgreich an das Referenzsystem gesendet.");
        } else {
            System.out.println("Fehler beim Senden der Daten an das Referenzsystem: " + response.getStatusCode());
        }
    }

    private ResultPayload createResultPayload(List<CustomerUsage> usageData) {
        return new ResultPayload(usageData);
    }

    static class ResultPayload {
        private List<CustomerUsage> result;

        public ResultPayload(List<CustomerUsage> result) {
            this.result = result;
        }

        public List<CustomerUsage> getResult() {
            return result;
        }

        public void setResult(List<CustomerUsage> result) {
            this.result = result;
        }
    }
}
