# Makefile for all documents
#
# $Id: Makefile,v 1.5 2003/10/03 21:56:54 valeks Exp $

JAVAPACKAGES= com.novel.odisp 
JAVAPACKAGES+= com.novel.odisp.common

TEXDOCS=impl.tex \
        tz.tex

TEXDVIS=$(TEXDOCS:.tex=.dvi)

TEXPROC=latex
JAVADOC=javadoc
JAVADOC_FOLDER= javadoc
JAVADOC_FLAGS= -version -author -d $(JAVADOC_FOLDER)

RM=rm -rf

all: $(TEXDVIS) javadoc

javadoc:
	@echo Generating javadoc...
	$(JAVADOC) $(JAVADOC_FLAGS) $(JAVAPACKAGES)

$(TEXDOCS:.tex=.dvi): $(@:.dvi=.tex)
	@echo Generating TeX docs...
	$(TEXPROC) $>
	$(TEXPROC) $>

clean:
	@echo Cleaning...
	$(RM) $(TEXDOCS:.tex=.log) $(TEXDOCS:.tex=.aux) $(TEXDOCS:.tex=.toc)

distclean: clean
	@echo Full clean...
	$(RM) $(TEXDOCS:.tex=.dvi) $(TEXDOCS:.tex=.toc)
	$(RM) $(JAVADOC_FOLDER)