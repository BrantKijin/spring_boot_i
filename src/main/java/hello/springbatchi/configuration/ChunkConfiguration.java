package hello.springbatchi.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class ChunkConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;


	@Bean
	public Job batchJob(){
		return jobBuilderFactory.get("batchJob")
			.start(helloStep1())
			//.next(helloStep2())
			.build();

	}

	@Bean
	public Step helloStep2() {
		return stepBuilderFactory.get("helloStep2")
			.tasklet(new Tasklet() {
				@Override
				public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws
					Exception {
					System.out.println("step helloStep2");
					return RepeatStatus.FINISHED;
				}
			}).build();
	}

	@Bean
	public Step helloStep1() {
		return stepBuilderFactory.get("helloStep1")
			.<String,String>chunk(5)
			.reader(new ListItemReader<>(List.of("item1","item2","item3","item4","item5")))
			.processor(new ItemProcessor<String, String>() {
				@Override
				public String process(String item) throws Exception {
					System.out.println("process"+ item);
					return "my " + item;
				}
			})
			.writer(new ItemWriter<String>() {
				@Override
				public void write(List<? extends String> list) throws Exception {
					System.out.println("writer:"+list);
				}
			})
			.build();
	}

}
