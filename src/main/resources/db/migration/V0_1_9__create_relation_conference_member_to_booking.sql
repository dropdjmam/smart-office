ALTER TABLE conference_members
    ADD CONSTRAINT FK_CONFERENCE_MEMBERS_ON_BOOKING FOREIGN KEY (booking_id) REFERENCES bookings (id);
