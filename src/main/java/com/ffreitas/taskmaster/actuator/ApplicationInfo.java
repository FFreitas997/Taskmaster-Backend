package com.ffreitas.taskmaster.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class ApplicationInfo implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        // Adding the application name to the info details
        builder.withDetail("ApplicationName", "Taskmaster")
                // Adding a description of the application to the info details
                .withDetail("Description", "This is a simple task management system")
                // Adding the version of the application to the info details
                .withDetail("Version", "1.0.0")
                // Adding the author's name to the info details
                .withDetail("Author", "Francisco Freitas")
                // Adding the author's LinkedIn profile to the info details
                .withDetail("LinkedIn", "https://www.linkedin.com/in/francisco-freitas-a289b91b3/")
                // Adding the author's GitHub profile to the info details
                .withDetail("Github", "https://github.com/FFreitas997");
    }
}
