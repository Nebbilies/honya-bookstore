package com.honya.bookstore.ticket.application;

import com.honya.bookstore.ticket.domain.Ticket;
import java.util.List;

public interface TicketService {
    Ticket createTicket(String userId, Ticket ticket);
    List<Ticket> getAllTickets();
    Ticket respondToTicket(Integer ticketId, String staffId, String response);
}