package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@RestController
@RequestMapping(value = "/api/v1/sse")
@CrossOrigin(origins = "http://localhost:4200")
public class SSEController {

    public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @ApiOperation(value = "Subscribe to the SEE", authorizations = {@Authorization(value = "apiKey")})
    @GetMapping(value="/subscribe", consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe(){
        SseEmitter sseEmitter = new SseEmitter();
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e){
            e.printStackTrace();
        }
        return sseEmitter;
    }

}
