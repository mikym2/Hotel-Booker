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
    //@Valid tells Spring to check specified rules in the ContactRequest record when it receives the request
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
         * and @ResponseStatus(HttpStatus.BAD_REQUEST) makes the response use HTTP status 400 Bad Request.
         * The returned ErrorResponse record is serialized as JSON, for example:
         * */
        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleBadRequest(IllegalArgumentException exception){
            return new ErrorResponse(exception.getMessage());
        }

        /**
        *
         * Step 1: exception.getBindingResult()
         *Retrieves the validation results, including information about fields that failed validation.
         *
         * Step 2: .getFieldErrors()
         *Retrieves the errors associated with individual fields.
         *For example:
         * - name → "Name is required."
         * - email → "Email must be valid."
         *
         *
         * Step 3: .stream()
         *Converts the list of errors into a Java Stream so you can process them using operations such as filter, map, and findFirst.
         *
         *
         * Step 4: .findFirst()
         *Selects the first error in the stream.
         *The result is an Optional because the stream might be empty.
         *
         *
         * Step 5: .map(error -> error.getDefaultMessage())
         *If an error exists, extracts its validation message.
         *For example, it extracts "Email must be valid." from the error object.
         *
         *
         * Step 6: .orElse("Invalid request.")
         *If there is no error message available in the Optional, uses "Invalid request." as a fallback.
        *
        * */
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
