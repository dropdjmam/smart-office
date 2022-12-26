package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TeamMemberInfoTeamDto {

    private Long id;

    private Long leaderId;

    private String leaderName;

    private String name;

    private Long membersNumber;

}
