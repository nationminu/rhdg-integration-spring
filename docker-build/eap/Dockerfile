FROM openjdk:11
#--------------------------------------------------------------------------#
#--------------------------------------------------------------------------#
#                      J B O S S E A P  v7.3.0  I M A G E                  #
#--------------------------------------------------------------------------#
#--------------------------------------------------------------------------#   
LABEL maintainer="mwsong@rockplace.co.kr"
 
ENV JBOSS_HOME=/usr/local/jboss-eap-7.3
ENV JBOSS_MODULEPATH=${JBOSS_HOME}/modules:/app/modules.ext/
ENV CONFIG_FILE=standalone.xml 

COPY jboss-eap-7.3.0.zip /usr/local/

RUN cd /usr/local && unzip jboss-eap-7.3.0.zip && rm -f jboss-eap-7.3.0.zip
RUN cd ${JBOSS_HOME}/bin && ./add-user.sh admin admin 

COPY docker-entrypoint.sh /bin/
RUN chmod +x /bin/docker-entrypoint.sh 
 
WORKDIR $JBOSS_HOME

EXPOSE 8080
EXPOSE 8009
EXPOSE 9990
 
ENTRYPOINT ["docker-entrypoint.sh"] 