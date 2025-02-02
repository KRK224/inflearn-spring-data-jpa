package study.data_jpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UserNameOnly {
    String getUsername(); // closed Projection.

    // open projection
    // 쿼리를 통해 모든 데이터 다 가져온 후에 조립하기 때문에 비효율적.
    @Value("#{target.username + ' ' + target.age}")
    String getUsernameAndAge();
}
