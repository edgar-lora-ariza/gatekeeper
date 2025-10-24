package com.white.label.gatekeeper.application.use.cases;

import com.white.label.gatekeeper.core.model.EncryptionKey;
import com.white.label.gatekeeper.core.model.SigningKey;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

public abstract class RotateUseCase {

  private final ApplicationContext applicationContext;

  protected RotateUseCase(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public void refreshBean(String beanName) {
    DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
        .getAutowireCapableBeanFactory();
    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

    beanFactory.destroySingleton(beanName);

    beanFactory.registerBeanDefinition(beanName, beanDefinition);
    if (beanName.equals("getActiveSigningKey")) {
      beanFactory.getBean(beanName, SigningKey.class);
    } else if (beanName.equals("getEncryptionKey")){
      beanFactory.getBean(beanName, EncryptionKey.class);
    } else {
      beanFactory.getBean(beanName);
    }
  }
}
