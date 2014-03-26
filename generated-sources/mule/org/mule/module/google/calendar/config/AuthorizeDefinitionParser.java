
package org.mule.module.google.calendar.config;

import javax.annotation.Generated;
import org.mule.module.google.calendar.processors.AuthorizeMessageProcessor;
import org.mule.security.oauth.config.AbstractDevkitBasedDefinitionParser;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

@Generated(value = "Mule DevKit Version 3.5.0-M4", date = "2014-03-26T12:31:10-05:00", comments = "Build M4.1875.17b58a3")
public class AuthorizeDefinitionParser
    extends AbstractDevkitBasedDefinitionParser
{


    public BeanDefinition parse(Element element, ParserContext parserContent) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(AuthorizeMessageProcessor.class.getName());
        parseConfigRef(element, builder);
        parseProperty(builder, element, "state");
        parseProperty(builder, element, "access_type");
        parseProperty(builder, element, "force_prompt");
        parseProperty(builder, element, "authorizationUrl");
        parseProperty(builder, element, "accessTokenUrl");
        parseProperty(builder, element, "accessTokenId");
        BeanDefinition definition = builder.getBeanDefinition();
        setNoRecurseOnDefinition(definition);
        attachProcessorDefinition(parserContent, definition);
        return definition;
    }

}
