//package com.ipos123.cms;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
//
//@Configuration
//@EntityScan(basePackageClasses= {Application.class,Jsr310JpaConverters.class})
//@SpringBootApplication
//public class Application {
//
//    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//    }
//}

package com.ipos123.cms;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ipos123.cms.common.Constants;
import com.ipos123.cms.domain.Order;
import com.ipos123.cms.dto.OrderDTO;
import com.ipos123.cms.util.DateUtil;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.velocity.VelocityEngineFactory;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan
@EnableAutoConfiguration
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static String[] ARRAY_UNIT_MINUTES = new String[]{"00", "15", "30", "45"};

    public static void main(String[] args) {
        byte[] encodedBytes = org.apache.commons.codec.binary.Base64.encodeBase64("2800019".getBytes());
        System.out.println("encodedBytes " + new String(encodedBytes));
        byte[] decodedBytes =  org.apache.commons.codec.binary.Base64.decodeBase64(encodedBytes);
        System.out.println("decodedBytes " + new String(decodedBytes));

        int fromHours = 8;
        int toHours = 20;
        List<String> timesLst = new ArrayList<>();

        for (int hours = fromHours; hours <= toHours; hours++) {
            String time = String.valueOf(hours);
            for (String tmp : ARRAY_UNIT_MINUTES) {
                String timeHHmm = (time.length() == 1 ? "0" + time : time) + ":" + tmp;
                timesLst.add(timeHHmm);
            }
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.MMddyyyy);
            Calendar calendar = Calendar.getInstance();
            if (sdf.format(calendar.getTime()).equals(sdf.format(new Date()))) {
                // check hours
                int hours = calendar.get(Calendar.HOUR_OF_DAY);

                for (int i = TimeRange.MIN_TIME; i < hours; i++) {
                    if (i < hours) {
                        for (int j = 0; j < ARRAY_UNIT_MINUTES.length; j++) {
                            StringBuilder time = new StringBuilder();
                            time.append(i < 10 ? "0" + i : i);
                            time.append(":");
                            time.append(ARRAY_UNIT_MINUTES[j]);

                            timesLst.remove(time.toString());
                        }
                    }
                }

                // check minutes
                int minutes = calendar.get(Calendar.MINUTE);

                for (int i = 0; i < ARRAY_UNIT_MINUTES.length; i++) {
                    Integer value = Integer.parseInt(ARRAY_UNIT_MINUTES[i]);
                    if (value.intValue() < minutes) {
                        StringBuilder time = new StringBuilder();
                        time.append(hours < 10 ? "0" + hours : hours);
                        time.append(":");
                        time.append(ARRAY_UNIT_MINUTES[i]);

                        timesLst.remove(time.toString());
                    }
                }
            }
        } catch (Exception e) {

        }

        // list excluded time
        List<String> lstExcludedTime = new ArrayList<>();
        List<String> lstDurationsTime = new ArrayList<>();

        List<OrderDTO> orders = new ArrayList<OrderDTO>();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTime("17:00");
        orderDTO.setDuration(30);

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        orderDTO.setDate(calendar.getTime());

        orders.add(orderDTO);

        for (OrderDTO order : orders) {
//            if (CUSTOMER_INFO != null && CUSTOMER_INFO.getId() != null
//                    && Objects.equals(CUSTOMER_INFO.getId(), order.getCustomer().getId())) {
//                continue;
//            }
            timesLst.remove(order.getTime());
            lstExcludedTime.add(order.getTime());

            // process duration time
            int countRepeat = 0;
            if (order.getDuration() != null) {
                countRepeat = order.getDuration().intValue() / 15;
                countRepeat = countRepeat - 1;
            }

            Date timeOrder = DateUtil.formatStringToDate(order.getTime(), Constants.HH_MM);
            Calendar cal = Calendar.getInstance();
            cal.setTime(timeOrder);

            while (countRepeat > 0) {
                cal.add(Calendar.MINUTE, 15);
                timesLst.remove(DateUtil.formatDate(cal.getTime(), Constants.HH_MM));
                countRepeat--;
            }

        }

        int duration = 45;
        for (String time : lstExcludedTime) {
            int countRepeat = duration / 15;

            Date timeOrder = DateUtil.formatStringToDate(time, Constants.HH_MM);
            Calendar cal = Calendar.getInstance();
            cal.setTime(timeOrder);

            while (countRepeat > 0) {
                cal.add(Calendar.MINUTE, -15);
                timesLst.remove(DateUtil.formatDate(cal.getTime(), Constants.HH_MM));
                countRepeat--;
            }
        }


        for (String time : timesLst) {
            System.out.println(time);
        }

    }

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		return application.sources(Application.class);
//	}
//
//	@Bean
//	public TaskExecutor taskExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(10);
//		executor.setMaxPoolSize(20);
//		executor.setQueueCapacity(30);
//		return executor;
//	}
//
//
//	@Bean
//    public VelocityEngine getVelocityEngine() throws VelocityException, IOException{
//		VelocityEngineFactory factory = new VelocityEngineFactory();
//        Properties props = new Properties();
//        props.put("resource.loader", "class");
//        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//        factory.setVelocityProperties(props);
//        return factory.createVelocityEngine();
//    }
}