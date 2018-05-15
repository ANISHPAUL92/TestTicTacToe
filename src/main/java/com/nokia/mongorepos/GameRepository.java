package com.nokia.mongorepos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nokia.mongodocs.Game;

public interface GameRepository extends MongoRepository<Game,Integer>{
    public Game findByGameID(int gameID);
}