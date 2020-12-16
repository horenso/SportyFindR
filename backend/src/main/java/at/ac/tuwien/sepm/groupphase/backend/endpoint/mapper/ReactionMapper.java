package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReactionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {MessageMapper.class})
public interface ReactionMapper {

    Reaction.ReactionType reactionDtoTypeToReactionType(ReactionDto.ReactionDtoType type);

    ReactionDto.ReactionDtoType reactionTypeToReactionDtoType(Reaction.ReactionType type);

    @Mapping(source = "messageId", target = "message.id")
    Reaction reactionDtoToReaction(ReactionDto reactionDto);

    @Mapping(source = "message.id", target = "messageId")
    ReactionDto reactionToReactionDto(Reaction reaction);
}
