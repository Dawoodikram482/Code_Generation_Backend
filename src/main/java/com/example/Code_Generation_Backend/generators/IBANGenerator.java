package com.example.Code_Generation_Backend.generators;

import com.example.Code_Generation_Backend.models.Account;
import org.hibernate.HibernateException;
import org.hibernate.id.IdentifierGenerator;
import java.util.Random;
import java.io.Serializable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;


public class IBANGenerator implements IdentifierGenerator {
  private final Random random;
  public IBANGenerator(Random random) {
    this.random = random;
  }
  @Override
  public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
    if (o instanceof Account account && account.getIban() != null) {
      return account.getIban();
    } else {
      return generateIban();
    }
  }
  private String generateIban(){
    long accountNumber = random.nextLong();
    String formattedAccountNumber = String.valueOf(Math.abs(accountNumber)).substring(0, 9);
    int randomDigits = random.nextInt(100);
    String formattedRandomDigits = String.format("%02d", randomDigits);
    return "NL" + formattedRandomDigits + "UNIB" + formattedAccountNumber;
  }

}
