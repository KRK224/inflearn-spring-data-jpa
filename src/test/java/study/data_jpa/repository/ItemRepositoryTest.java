package study.data_jpa.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.data_jpa.entity.Item;

@SpringBootTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save() {
        // 직접 Id를 생성해서 주입하는 경우, SimpleJpaRepository의 save() 메서드 내부
        // entityInformation.isNew(entity) 메서드에서 Id가 이미 존재하는지 확인할 때 Id가 존재하므로
        // merge() 메서드를 호출하게 된다.
        // 이를 방지하기 위해, Item 객체에 Persistable 인터페이스를 구현하여 isNew() 메서드를 오버라이딩한다.
        Item item = new Item("A");
        itemRepository.save(item);
    }

}