package com.cst438.services;


import com.cst438.controllers.EnrollmentController;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;


public class RegistrationServiceMQ extends RegistrationService {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EnrollmentController enrollmentController;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public RegistrationServiceMQ() {
        System.out.println("MQ registration service ");
    }

    // ----- configuration of message queues

    @Autowired
    Queue registrationQueue;


    // ----- end of configuration of message queue

    // receiver of messages from Registration service

    @RabbitListener(queues = "gradebook-queue")
    @Transactional
    public void receive(EnrollmentDTO enrollmentDTO) {
        System.out.println("received MQ enrollment " + enrollmentDTO.id);
        enrollmentController.addEnrollment(enrollmentDTO);
    }

    // sender of messages to Registration Service
    @Override
    public void sendFinalGrades(int course_id, CourseDTOG courseDTO) {
        System.out.println("sending final grades via MQ");
        rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTO);
    }

}
