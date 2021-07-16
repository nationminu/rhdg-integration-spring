FROM openjdk:11
#--------------------------------------------------------------------------#
#--------------------------------------------------------------------------#
#                      J B O S S E A P  v7.3.0  I M A G E                  #
#--------------------------------------------------------------------------#
#--------------------------------------------------------------------------#   
LABEL maintainer="mwsong@rockplace.co.kr"
 
ENV JDG_HOME=/usr/local/redhat-datagrid-8.2.0-server  

COPY redhat-datagrid-8.2.0-server.zip /usr/local/

RUN cd /usr/local && unzip redhat-datagrid-8.2.0-server.zip && rm -f redhat-datagrid-8.2.0-server.zip 
RUN cd ${JDG_HOME}/bin && ./cli.sh user create admin -p admin -g admin

COPY docker-entrypoint.sh /bin/
RUN chmod +x /bin/docker-entrypoint.sh 
 
WORKDIR $JDG_HOME

EXPOSE 11222 
 
ENTRYPOINT ["docker-entrypoint.sh"] 