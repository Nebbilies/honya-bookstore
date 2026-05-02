package com.honya.bookstore.ticket;

import com.honya.bookstore.ticket.Ticket;
import java.util.List;

public interface TicketService {
    Ticket createTicket(String userId, Ticket ticket);
    List<Ticket> getAllTickets();
    Ticket respondToTicket(Integer ticketId, String staffId, String response);
}