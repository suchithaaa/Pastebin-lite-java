package com.pastebin.pastebinlite.repository;

import com.pastebin.pastebinlite.model.Paste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasteRepository extends JpaRepository<Paste, String> {
}
