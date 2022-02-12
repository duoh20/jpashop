package study.datajpa.repository;

import lombok.Getter;

public interface MemberProjection {

    Long getId();
    String getName();
    String getTeamName();
}
