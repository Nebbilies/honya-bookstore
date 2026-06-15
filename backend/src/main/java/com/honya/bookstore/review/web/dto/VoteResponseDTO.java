package com.honya.bookstore.review.web.dto;

public record VoteResponseDTO(
        int voteCount,
        String userVote
) {
}
