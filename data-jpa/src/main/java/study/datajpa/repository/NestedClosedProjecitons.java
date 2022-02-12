package study.datajpa.repository;

public interface NestedClosedProjecitons {

    String getName();
    TeamInfo getTeam(); //member가 가진 Team의 이름을 가져오도록 인터페이스 작성

    interface TeamInfo {
        String getName();
    }
}
