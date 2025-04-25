package tf.cyber.resourcemanager.controller;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tf.cyber.resourcemanager.errors.HTTPError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class CustomErrorController implements ErrorController {
    private ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    public HTTPError handleError(HttpServletRequest request, HttpServletResponse response) {
        return new HTTPError(
                response.getStatus(),
                HttpStatus.resolve(response.getStatus()).getReasonPhrase()
        );
    }
}
