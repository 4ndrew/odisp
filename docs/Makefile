# Makefile for all documents
#
# $Id: Makefile,v 1.17 2004/01/27 12:57:26 valeks Exp $

# folders & files
TEXDOCS=impl.tex gui_imp.tex tz.tex style-9.tex odisp-style.tex
TEXPDFS=$(TEXDOCS:.tex=.pdf)
TEXHTMLS=$(TEXDOCS:.tex=.html)
RELEASEDIR=/mnt/docs/Оборудование\ Новел/Проект\ МЦП-155\ Н/Документация/NMS/

# tools
TEXPROC=pdflatex
HTMLPROC=latex2html -verbosity 0
RM=rm -rf
MV=mv

${TEXPDFS}: ${TEXDOCS}
	${TEXPROC} ${.TARGET:.pdf=.tex}
	${TEXPROC} ${.TARGET:.pdf=.tex}

${TEXHTMLS}: ${TEXDOCS}
	mkdir -p html/${.TARGET:.html=}
	$(HTMLPROC) -rootdir html/${.TARGET:.html=} ${.TARGET:.html=.tex}

clean:
	@echo Cleaning...
	$(RM) $(TEXDOCS:.tex=.log) $(TEXDOCS:.tex=.aux) $(TEXDOCS:.tex=.toc)

distclean: clean
	@echo Full clean...
	$(RM) $(TEXPDFS)
	$(RM) html
	$(RM) javadoc

release: ${TEXPDFS}
	@echo Releasing all pdfs
	$(MV) $(TEXPDFS) $(RELEASEDIR)

pdf: ${TEXPDFS}

htm: $(TEXHTMLS)

all: pdf htm
