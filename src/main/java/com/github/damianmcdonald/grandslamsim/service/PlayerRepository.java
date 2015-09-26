package com.github.damianmcdonald.grandslamsim.service;

import org.springframework.data.repository.CrudRepository;

import com.github.damianmcdonald.grandslamsim.domain.Player;

public interface PlayerRepository extends CrudRepository<Player, Long> {

}