package com.ra.busBooking.controller;

import com.ra.busBooking.model.BusData;
import com.ra.busBooking.model.User;
import com.ra.busBooking.repository.BusDataRepository;
import com.ra.busBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/adminScreen")
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BusDataRepository busDataRepository;


    @ModelAttribute("busDetails")
    public BusData busData() {
        return new BusData();
    }

    @GetMapping
    public String displayDashboard(Model model){
        String user= returnUsername();
        model.addAttribute("userDetails", user);
        return "adminScreen";
    }

    private String returnUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
        User users = userRepository.findByEmail(user.getUsername());
        return users.getName();
    }
    //create post mapping using save allBusdata
    @PostMapping
    public String saveBusData(@ModelAttribute("busDetails") BusData busData, Model model){
        String user= returnUsername();
        model.addAttribute("userDetails", user);
        busDataRepository.save(busData);
        model.addAttribute("busDetails", new BusData());
        return "redirect:/adminScreen?success";
    }
    //create get mapping using get all records of bus details
    @GetMapping("/allRecords")
    public String getAllRecords(Model model){
        List<BusData> data = busDataRepository.findAll();
        String user= returnUsername();
        model.addAttribute("userDetails", user);
        model.addAttribute("data", data);
        return "allRecords";
    }
    //Get mapping using delete by id of bus records are deleted
    @GetMapping("/delete/{id}")
    public String getDataAfterDelete(@PathVariable int id, Model model){
        busDataRepository.deleteById(id);
        List<BusData> data = busDataRepository.findAll();
        String user= returnUsername();
        model.addAttribute("userDetails", user);
        model.addAttribute("data", data);
        return "redirect:/adminScreen/allRecords?success";
    }
}
