package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.HashtagDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface HashtagMapper {

    HashtagDto hashtagToHashtagDto(Hashtag hashtag);

    Hashtag hashtagDtoToHashtag(HashtagDto hashtag);

    List<HashtagDto> entityToListDto(List<Hashtag> hashtags);

}
