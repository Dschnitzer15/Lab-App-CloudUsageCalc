package Calculator;

import CustomerData.Customer;
import CustomerData.CustomerUsage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsageService {

    public List<CustomerUsage> aggregateUsage(List<Customer> customers) {
        Map<String, Long> startTimes = new HashMap<>();
        Map<String, Long> totalConsumption = new HashMap<>();

        for (Customer customer : customers) {
            String key = customer.getCustomerId() + "-" + customer.getWorkloadId();
            if ("start".equalsIgnoreCase(customer.getEventType())) {
                startTimes.put(key, customer.getTimestamp());
            } else if ("stop".equalsIgnoreCase(customer.getEventType())) {
                Long startTime = startTimes.get(key);
                if (startTime != null) {
                    long runtime = customer.getTimestamp() - startTime;
                    totalConsumption.put(customer.getCustomerId(),
                            totalConsumption.getOrDefault(customer.getCustomerId(), 0L) + runtime);
                    startTimes.remove(key);
                }
            }
        }

        List<CustomerUsage> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : totalConsumption.entrySet()) {
            result.add(new CustomerUsage(entry.getKey(), entry.getValue()));
        }
        return result;
    }
}
