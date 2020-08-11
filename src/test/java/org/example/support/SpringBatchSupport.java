package org.example.support;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.AbstractStep;
import org.springframework.batch.core.step.StepLocator;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * Spring Batch Test Support
 */
public class SpringBatchSupport {

    public static JobExecution launchJob(String jobName) throws Exception {
        jobLauncherTestUtils.setJob(setStepProperty(jobName));
        return jobLauncherTestUtils.launchJob();
    }

    public static JobExecution launchJob(String jobName, JobParameters jobParameters) throws Exception {
        jobLauncherTestUtils.setJob(setStepProperty(jobName));
        return jobLauncherTestUtils.launchJob(jobParameters);
    }

    public static JobExecution launchStep(String jobName, String stepName) throws Exception {
        jobLauncherTestUtils.setJob(setStepProperty(jobName));
        return jobLauncherTestUtils.launchStep(stepName);
    }

    public static JobExecution launchStep(String jobName, String stepName, JobParameters jobParameters) throws Exception {
        jobLauncherTestUtils.setJob(setStepProperty(jobName));
        return jobLauncherTestUtils.launchStep(stepName, jobParameters);
    }

    public static JobExecution launchStep(String jobName, String stepName, JobParameters jobParameters, ExecutionContext executionContext) throws Exception {
        jobLauncherTestUtils.setJob(setStepProperty(jobName));
        return jobLauncherTestUtils.launchStep(stepName, jobParameters, executionContext);
    }

    public static RepeatStatus launchTasklet(String jobName, String stepName) throws Exception {
        return _launchTasklet(jobName, stepName, MetaDataInstanceFactory.createStepExecution());
    }

    public static RepeatStatus launchTasklet(String jobName, String stepName, JobParameters jobParameters) throws Exception {
        return _launchTasklet(jobName, stepName, MetaDataInstanceFactory.createStepExecution(jobParameters));
    }

    public static RepeatStatus launchTasklet(String jobName, String stepName, JobParameters jobParameters, ExecutionContext executionContext) throws Exception {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters, executionContext);
        stepExecution.getJobExecution().setExecutionContext(executionContext);
        return _launchTasklet(jobName, stepName, stepExecution);
    }

    public static <T> T getTasklet(String jobName, String stepName) throws Exception {
        return _getTasklet(jobName, stepName, MetaDataInstanceFactory.createStepExecution());
    }

    public static <T> T getTasklet(String jobName, String stepName, JobParameters jobParameters) throws Exception {
        return _getTasklet(jobName, stepName, MetaDataInstanceFactory.createStepExecution(jobParameters));
    }

    public static <T> T getTasklet(String jobName, String stepName, JobParameters jobParameters, ExecutionContext executionContext) throws Exception {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters, executionContext);
        stepExecution.getJobExecution().setExecutionContext(executionContext);
        return _getTasklet(jobName, stepName, stepExecution);
    }

    public static StepContribution getStepContribution() {
        return getStepContext().getStepExecution().createStepContribution();
    }

    public static ChunkContext getChunkContext() {
        return new ChunkContext(getStepContext());
    }

    public static StepContext getStepContext() {
        return StepSynchronizationManager.getContext();
    }

    // 启动 tasklet step 中的 tasklet 类
    private static RepeatStatus _launchTasklet(String jobName, String stepName, StepExecution stepExecution) throws Exception {
        StepContext stepContext = StepSynchronizationManager.register(stepExecution);
        StepContribution stepContribution = stepContext.getStepExecution().createStepContribution();
        ChunkContext chunkContext = new ChunkContext(stepContext);
        Tasklet tasklet = _getTasklet(jobName, stepName);
        return tasklet.execute(stepContribution, chunkContext);
    }

    // 获取 tasklet, 转换成目标类
    private static <T> T _getTasklet(String jobName, String stepName, StepExecution stepExecution) throws Exception {
        StepSynchronizationManager.register(stepExecution);
        return (T) ProxyUtil.getTarget(_getTasklet(jobName, stepName));
    }

    // 获取 tasklet
    private static <T> T _getTasklet(String jobName, String stepName) throws Exception {
        StepLocator stepLocator = (StepLocator) jobRegistry.getJob(jobName);
        Step step = stepLocator.getStep(stepName);
        if (step == null) {
            throw new IllegalStateException("No Step found with name: [" + stepName + "]");
        }
        TaskletStep taskletStep = null;
        try {
            taskletStep = (TaskletStep) step;
        } catch (ClassCastException e) {
            throw new IllegalStateException("step is not a tasklet step");
        }
        return (T) taskletStep.getTasklet();
    }

    // 设置 step 中的 JobRepository
    private static Job setStepProperty(String jobName) throws NoSuchJobException {
        AbstractJob abstractJob = (AbstractJob) jobRegistry.getJob(jobName);
        abstractJob.setJobRepository(jobLauncherTestUtils.getJobRepository());
        StepLocator stepLocator = (StepLocator) abstractJob;
        Collection<String> stepNames = stepLocator.getStepNames();
        if (!CollectionUtils.isEmpty(stepNames)) {
            for (String stepName : stepNames) {
                Step step = stepLocator.getStep(stepName);
                if (step == null) {
                    step = stepLocator.getStep(jobName + "." + stepName);
                }
                if (step == null) {
                    throw new IllegalStateException("No Step found with name: [" + stepName + "]");
                } else {
                    AbstractStep abstractStep = (AbstractStep) step;
                    abstractStep.setJobRepository(jobLauncherTestUtils.getJobRepository());
                }
            }
        }
        return abstractJob;
    }

    private static JobRegistry jobRegistry;
    private static JobLauncherTestUtils jobLauncherTestUtils;

    /**
     * 为 SpringBatchSupport 初始化一些 SpringBatch 框架中的基础类
     */
    @Component
    private static class InitBasicClassFor_springBatchSupport implements InitializingBean {

        @Resource
        PlatformTransactionManager platformTransactionManager;

        @Resource
        public void setJobRegistry(JobRegistry jobRegistry) {
            SpringBatchSupport.jobRegistry = jobRegistry;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            // new map job repository
            MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean = new MapJobRepositoryFactoryBean(platformTransactionManager);
            mapJobRepositoryFactoryBean.setValidateTransactionState(false);
            JobRepository jobRepository = mapJobRepositoryFactoryBean.getObject();

            // new job launcher
            SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
            simpleJobLauncher.setJobRepository(jobRepository);
            simpleJobLauncher.afterPropertiesSet();

            // construct JobLauncherTestUtils
            SpringBatchSupport.jobLauncherTestUtils = new JobLauncherTestUtils();
            SpringBatchSupport.jobLauncherTestUtils.setJobLauncher(simpleJobLauncher);
            SpringBatchSupport.jobLauncherTestUtils.setJobRepository(jobRepository);
        }

    }

}
