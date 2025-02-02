package study.data_jpa.repository;

public class UsernameOnlyDto {
    private String username;

    // 생성자의 파라미터로 entity와 동일한 이름 사용할 것.
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
