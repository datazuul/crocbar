# CrocBar Makefile
# Tested under Linux *only*



TARGET_ARCH = linux

JAVA = java
JAVAC  = javac
JAR    = jar
JAVADOC = javadoc
TAR = tar
DEBUG  = -g
CLASSPATH = -classpath
SOURCEPATH = -sourcepath
PACKAGE = uicrocbar crocwidget myutil
BUILDER = builder.jar
BUILD_INFO = build.txt
BUILD_TO_MODIFY = src/ui/DefaultText.java
CROCBAR_BINARY = crocbar.jar
CROCBAR_JAR_TXT = crocbar.txt
CROCBAR_CONFIG = config.xml
CROCBAR_CONFIG_SRC = config.xml
CROCBAR_PUB = ~/pub/

#Variable that points to TTool installation Path
CROCBAR_PATH := $(shell /bin/pwd)
CROCBAR_SRC = $(CROCBAR_PATH)/src
CROCBAR_BIN = $(CROCBAR_PATH)/bin
CROCBAR_DOC = $(CROCBAR_PATH)/doc
JAVAMAIL_JAR =$(CROCBAR_BIN)/mail.jar


all: javasrc thebin

javasrc:
#	date
#	svn update build.txt src/ui/DefaultText.java
#	$(JAVA) -jar $(BUILDER) $(BUILD_INFO) $(BUILD_TO_MODIFY)
#	svn commit build.txt src/ui/DefaultText.java -m 'update on build version: builder.txt'
	$(JAVAC) $(CLASSPATH) $(JAVAMAIL_JAR) $(SOURCEPATH) $(CROCBAR_SRC) $(CROCBAR_SRC)/*.java $(CROCBAR_SRC)/*/*.java

thebin:
	rm -f $(CROCBAR_BIN)/$(CROCBAR_BINARY)
	cd $(CROCBAR_SRC);  $(JAR) cmf $(CROCBAR_JAR_TXT) $(CROCBAR_BIN)/$(CROCBAR_BINARY) CrocBar.class crocwidget/*.class myutil/*.class uicrocbar/*.class  crocwidget/images/*
#javax/mail/*.class javax/mail/event/*.class  javax/mail/internet/*.class javax/mail/search/*.class javax/mail/util/*.class com/sun/mail/*/*.class com/sun/mail/*/*/*.class
#	cp $(CROCBAR_SRC)/$(CROCBAR_CONFIG_SRC) $(CROCBAR_BIN)/

documentation:
	$(JAVADOC) $(CLASSPATH) $(CROCBAR_SRC) -d $(CROCBAR_DOC) $(CROCBAR_SRC)/*.java $(CROCBAR_SRC)/*/*.java 

release: crocbarjar  
	echo release done

clean:
	rm -f $(CROCBAR_SRC)/*.class $(TTOOL_SRC)/*.java~
	@@for p in $(PACKAGE); do \
		echo rm -f $$p/*.class;\
		rm -f $(CROCBAR_SRC)/$$p/*.class $(CROCBAR_SRC)/$$p/*.java~; \
	done

