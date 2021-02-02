package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.HashtagRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleHashtagService implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final String hashtagPattern = "(#[A-Za-z0-9]+)";

    @Override
    public Hashtag getByName(String name) {
        return hashtagRepository.getHashtagByNameEquals(name);
    }

    @Override
    public Hashtag getOneById(Long id) {
        return hashtagRepository.getOneById(id);
    }

    @Override
    public Hashtag create(Hashtag hashtag) {
        return hashtagRepository.save(hashtag);
    }

    @Override
    public void acquireHashtags(Message message){
        String[] words = message.getContent().split("\\s+");
        List<String> hashtags = new ArrayList<>();
        for(String word : words){
            if (Pattern.matches(hashtagPattern, word)){
                hashtags.add(word.substring(1));
            }
        }

        for(String hashtag : hashtags){
            if (hashtagRepository.findHashtagByName(hashtag).isPresent()){
                Hashtag hashtag1 = hashtagRepository.findHashtagByName(hashtag).get();
                hashtag1.addMessage(message);
                hashtagRepository.save(hashtag1);
            } else {
                Hashtag hashtag1 = new Hashtag(hashtag);
                hashtag1.addMessage(message);
                hashtagRepository.save(hashtag1);
            }
        }
    }

    @Override
    public void acquireHashtags(Spot spot) {
        if(spot.getDescription()!=null) {
            String hashtagPattern = "(?:^|\\s|[\\p{Punct}&&[^/]])(#[\\p{L}0-9-_]+)";
            String[] words = spot.getDescription().split("\\s+");
            List<String> hashtags = new ArrayList<>();
            for (String word : words) {
                if (Pattern.matches(hashtagPattern, word)) {
                    hashtags.add(word.substring(1));
                }
            }

            for (String hashtag : hashtags) {
                if (hashtagRepository.findHashtagByName(hashtag).isPresent()) {
                    Hashtag hashtag1 = hashtagRepository.findHashtagByName(hashtag).get();
                    hashtag1.addSpot(spot);
                    hashtagRepository.save(hashtag1);
                } else {
                    Hashtag hashtag1 = new Hashtag(hashtag);
                    hashtag1.addSpot(spot);
                    hashtagRepository.save(hashtag1);
                }
            }
        }
    }

    @Override
    public void deleteMessageInHashtags(Message message){
        List<Hashtag> hashtags = hashtagRepository.findHashtagsByMessagesListContains(message);
        for (Hashtag hashtag : hashtags){
            hashtag.deleteMessage(message.getId());
            if (hashtag.getMessagesList().isEmpty() && hashtag.getSpotsList().isEmpty()){
                hashtagRepository.delete(hashtag);
            } else {
                hashtagRepository.save(hashtag);
            }
        }
    }

    @Override
    public void deleteSpotInHashtags(Spot spot) {
        List<Hashtag> hashtags = hashtagRepository.findHashtagsBySpotsListContains(spot);
        for (Hashtag hashtag : hashtags){
            hashtag.deleteSpot(spot.getId());
            if (hashtag.getMessagesList().isEmpty() && hashtag.getSpotsList().isEmpty()){
                hashtagRepository.delete(hashtag);
            } else {
                hashtagRepository.save(hashtag);
            }
        }
    }

    @Override
    public List<Hashtag> findAll() {
       log.debug("Get all hashtags.");
        return hashtagRepository.findAll();
    }

    @Override
    public List<Hashtag> searchByName(String name) {

        if (name == null || name.equals("")) {
            return Collections.emptyList();
        }

        return hashtagRepository.searchByName(name);
    }

}
