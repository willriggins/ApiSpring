package com.theironyard;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by will on 7/25/16.
 */
@RestController
public class ApiController {
    static final String API_URL = "http://gturnquist-quoters.cfapps.io/api/random";

    @PostConstruct
    public void init() {


        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Hello");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }
    @Async
    public Future<HashMap> requestQuote() {
        // see more complex example here:
        // https://github.com/Book-It/BookIt (get exact link for example from tiy chs link fork)
        RestTemplate query = new RestTemplate();
        HashMap result = query.getForObject(API_URL, HashMap.class);
        String type = (String) result.get("type");
        if (type.equals("success")) {
            HashMap value = (HashMap) result.get("value");
            return new AsyncResult<>(value);
        }
        return null;
    }

    @RequestMapping(path = "/quote", method = RequestMethod.GET)
    public ArrayList getQuote() throws InterruptedException, ExecutionException {
        Future<HashMap> quote1 = requestQuote();
        Future<HashMap> quote2 = requestQuote();
        Future<HashMap> quote3 = requestQuote();

        while (!quote1.isDone() || !quote2.isDone() || !quote3.isDone()) {
            Thread.sleep(100);
        }

        ArrayList arr = new ArrayList();
        arr.add(quote1.get());
        arr.add(quote2.get());
        arr.add(quote3.get());


        return null;
    }
}
