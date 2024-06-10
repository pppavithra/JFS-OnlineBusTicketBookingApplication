package com.ra.busBooking.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ra.busBooking.helper.ObjectCreationHelper;
import com.ra.busBooking.service.DefaultUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ra.busBooking.DTO.BookingsDTO;
import com.ra.busBooking.model.Bookings;
import com.ra.busBooking.model.User;
import com.ra.busBooking.repository.BookingsRepository;
import com.ra.busBooking.repository.UserRepository;

@Controller
@RequestMapping("/myBooking")
public class MyBookingController {

	private DefaultUserService userService;

	public MyBookingController(DefaultUserService userService) {
		super();
		this.userService = userService;
	}
	@Autowired
	BookingsRepository bookingsRepository;
	
	@Autowired
	UserRepository userRepository;
	
	 @ModelAttribute("bookings")
	    public BookingsDTO bookingDto() {
	        return new BookingsDTO();
	    }

		//get mapping using user details and bus booking details
		@GetMapping
		public String login(Model model) {
			SecurityContext securityContext = SecurityContextHolder.getContext();
	        UserDetails users = (UserDetails) securityContext.getAuthentication().getPrincipal();
	        User user =userRepository.findByEmail(users.getUsername());
			List<Bookings> bs = bookingsRepository.findByUserId(user.getId());
			model.addAttribute("userDetails", user.getName());
			Collections.reverse(bs);
			model.addAttribute("bookings",bs);
			return "myBookings";
		}
    //generate ticket send  to user email
	@GetMapping("/generate/{id}")
	public String bookPage(@PathVariable int id,Model model) {
		Optional<Bookings> busdata = bookingsRepository.findById(id);
		Optional<User> users =userRepository.findById(busdata.get().getUserId());
		String user = users.get().getName();
		BookingsDTO bks = ObjectCreationHelper.createBookingsDTO(busdata.get());
		userService.sendEmail(bks, users.get(),busdata.get().getFileName());
		model.addAttribute("userDetails", user);
		List<Bookings> bs = bookingsRepository.findByUserId(users.get().getId());
		Collections.reverse(bs);
		model.addAttribute("bookings",bs);
		return "redirect:/myBooking?success";
	}
    //If user the bus booking trip is  cancellation so  tripstatus are cancelled
	@GetMapping("/cancel/{id}")
	public String cancelBooking(@PathVariable int id,Model model) {
		Optional<Bookings> busdata = bookingsRepository.findById(id);
		if(busdata.get().isTripStatus()==false) {
			setData(busdata.get(),model);
			return "redirect:/myBooking?alreadyCancel";
		}else {
			setData(busdata.get(),model);
			busdata.get().setTripStatus(false);
			bookingsRepository.save(busdata.get());

			return "redirect:/myBooking?successCancel";

		}
	}

	private void setData(Bookings busData, Model model) {
		Optional<User> users =userRepository.findById(busData.getUserId());
		List<Bookings> bs = bookingsRepository.findByUserId(users.get().getId());
		Collections.reverse(bs);
		model.addAttribute("bookings",bs);
	}


}
