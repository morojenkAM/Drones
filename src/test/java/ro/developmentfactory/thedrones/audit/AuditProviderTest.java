package ro.developmentfactory.thedrones.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import ro.developmentfactory.thedrones.testconfig.BaseRepositoryTest;
import ro.developmentfactory.thedrones.testconfig.TestAuditingConfig;

public class AuditProviderTest extends BaseRepositoryTest {

    @Autowired
    private AuditorAware<String> auditorAware;

    @Test
    @DisplayName("Validates the current test auditor.")
    public void validateAuditor() {
        String currentAuditor = auditorAware.getCurrentAuditor()
                .orElse(null);

        Assertions.assertEquals(TestAuditingConfig.TEST_AUDITOR, currentAuditor);
    }
}
