# Makefile for all documents
#
# $Id: Makefile,v 1.26 2004/08/26 08:33:37 valeks Exp $

# folders & files
TEXDOCS=odisp-style.tex todo.tex model.tex 
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
	$(RM) $(TEXDOCS:.tex=.log) $(TEXDOCS:.tex=.aux) $(TEXDOCS:.tex=.toc) $(TEXDOCS:.tex=.out)

distclean: clean
	@echo Full clean...
	$(RM) $(TEXPDFS)
.for fname in ${TEXDOCS:.tex=}
	$(RM) html/${fname}
.endfor
	$(RM) javadoc

release: ${TEXPDFS}
	@echo Releasing all pdfs
	$(MV) $(TEXPDFS) $(RELEASEDIR)

pdf: ${TEXPDFS}

htm: $(TEXHTMLS)

all: pdf htm
