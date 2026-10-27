package com.example.backend.controller;

import com.example.backend.JPA.entities.ContactMessage;
import com.example.backend.service.ContactMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

//tells Spring that this class handles API requests and returns data, rather than rendering HTML pages.
@RestController
@RequestMapping("/api")
public class HelloController {

    private final ContactMessageService contactMessageService;

    public HelloController(ContactMessageService contactMessageService){
        this.contactMessageService = contactMessageService;
    }

    @GetMapping("/hello")
    public Greeting hello(){
        return new Greeting("Hello from Spring Boot!");
    }

    @PostMapping("/contact")
    //@RequestBody tells Spring to convert the incoming JSON into a Java object.
    //@Valid tells Spring to check specified rules in the ContactRequestrecord when it receives the request
    public ContactResponse submitContact(@Valid @RequestBody ContactRequest request){

        ContactMessage saved = contactMessageService.create(request.name(), request.message());

        return new ContactResponse( "Message received successfully " + saved.getName() + "!");
    }

    @GetMapping("/contacts")
    public List<ContactMessage> getContacts(){
        return  contactMessageService.findAll();
    }

    public record Greeting(String message){}

    public record ContactResponse(String message){}

    public  record ContactRequest(
        @NotBlank(message = "Name is required.")
        @Size(max = 100, message = "Name cannot exceed 100 characters.")
        String name,

        @NotBlank(message = "Message is required.")
        @Size(max = 2000, message = "Message cannot exceed 2000 characters.")
        String message
    ){}

    /**
     * @RestControllerAdvice marks ApiExceptionHandler as centralized exception handling for REST controllers.
     * When a controller throws an exception, Spring looks for a matching handler method here.
     *
     * */
    @RestControllerAdvice
    class  ApiExceptionHandler{

        /**
         * @ExceptionHandler(IllegalArgumentException.class) handles IllegalArgumentExceptions,
         *      * and @ResponseStatus(HttpStatus.BAD_REQUEST) makes the response use HTTP status 400 Bad Request.
         *      * The returned ErrorResponse record is serialized as JSON, for example:
         * */
        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleBadRequest(IllegalArgumentException exception){
            return new ErrorResponse(exception.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public  ErrorResponse handleValidationError( MethodArgumentNotValidException exception){

            String errorMessage = exception.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .findFirst()
                    .map(error -> error.getDefaultMessage())
                    .orElse("Invalid request.");

            return new ErrorResponse(errorMessage);
        }
    }
    public record ErrorResponse(String error){}
}
