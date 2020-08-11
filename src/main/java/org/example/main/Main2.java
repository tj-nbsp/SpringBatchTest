package org.example.main;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main2 {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("job-context.xml");
        SimpleJobLauncher simpleJobLauncher = (SimpleJobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("billJob");
        JobExecution jobExecution = simpleJobLauncher.run(job, new JobParameters());
        System.out.println(jobExecution.toString());
    }

}
