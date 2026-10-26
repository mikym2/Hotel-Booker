package com.example.backend;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

//tells Spring that this class handles API requests and returns data, rather than rendering HTML pages.
@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Greeting hello(){
        return new Greeting("Hello from Spring Boot!");
    }

    @PostMapping("/contact")
    //@RequestBody tells Spring to convert the incoming JSON into a Java object.
    public ContactResponse submitContact(@RequestBody ContactRequest request){

        if(request.name() == null || request.name().isBlank()){
            throw new IllegalArgumentException("Name is required");
        }

        if(request.message() == null || request.message().isBlank()){
            throw new IllegalArgumentException("Message is required");
        }

        return new ContactResponse( "Message received successfully " + request.name() + "!");
    }

    public record Greeting(String message){}

    public record ContactResponse(String message){}

    public  record ContactRequest(String name, String message){}

    /**
     * @RestControllerAdvice marks ApiExceptionHandler as centralized exception handling for REST controllers.
     * When a controller throws an exception, Spring looks for a matching handler method here.
     *
     * In your code, @ExceptionHandler(IllegalArgumentException.class) handles IllegalArgumentExceptions,
     * and @ResponseStatus(HttpStatus.BAD_REQUEST) makes the response use HTTP status 400 Bad Request.
     * The returned ErrorResponse record is serialized as JSON, for example:
     *
     * */
    @RestControllerAdvice
    class  ApiExceptionHandler{

        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleBadRequest(IllegalArgumentException exception){
            return new ErrorResponse(exception.getMessage());
        }
    }
    public record ErrorResponse(String error){}
}
