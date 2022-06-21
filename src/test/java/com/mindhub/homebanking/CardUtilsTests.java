package com.mindhub.homebanking;

import com.mindhub.homebanking.utils.CardUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class CardUtilsTests {

    @Test
    public void createdRandomNumberBoth(){
        int number = CardUtils.getRandomNumber(1,1000);
        assertThat(number, both(greaterThan(0)).and(lessThan(1000)));
    }

    @Test
    public void RandomNumber(){
        int number = CardUtils.contarCifras(CardUtils.getRandomNumber(1,100));
        assertThat(number, isA(int.class));
    }



}
