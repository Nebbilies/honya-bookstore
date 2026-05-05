package com.honya.bookstore.ticket.infrastructure.persistence;

import com.honya.bookstore.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
}