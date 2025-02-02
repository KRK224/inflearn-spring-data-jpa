package study.data_jpa.repository;

public interface NestedClosedProjection {
    String getUsername();
    TeamInfo getTeam();

    // 중첩 Projection에서는 select 쿼리 최적화가 이뤄지지 않는다.
    interface TeamInfo {
        String getName();
    }
}
