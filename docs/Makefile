# Makefile for all documents
#
# $Id: Makefile,v 1.15 2003/12/15 12:13:48 valeks Exp $

# folders & files
TEXDOCS=impl.tex \
			gui_imp.tex \
			tz.tex \
			style-9.tex \
			odisp-style.tex
TEXDVIS=$(TEXDOCS:.tex=.dvi)
TEXPDFS=$(TEXDOCS:.tex=.pdf)
TEXHTMLS=$(TEXDOCS:.tex=.html)
RELEASEDIR=/mnt/docs/Оборудование\ Новел/Проект\ МЦП-155\ Н/Документация/NMS/

# tools
TEXPROC=latex
PDFPROC=dvipdfm
HTMLPROC=latex2html
FIND=find
RM=rm -rf
MV=mv

all: tex pdf html

tex: $(TEXDVIS)

pdf: $(TEXPDFS)

htm: $(TEXHTMLS)
	$(HTMLPROC) -rootdir html/ -local_icons $>

${TEXPDFS}: ${TEXDVIS}
	$(PDFPROC) ${.TARGET:.pdf=}

${TEXHTMLS}: 
	mkdir -p html/${.TARGET:.html=}
	$(HTMLPROC) -rootdir html/${.TARGET:.html=} ${.TARGET:.html=.tex}

$(TEXDOCS:.tex=.dvi): $(@:.dvi=.tex)
	@echo Generating TeX docs...
	$(TEXPROC) $>
	$(TEXPROC) $>

clean:
	@echo Cleaning...
	$(RM) $(TEXDOCS:.tex=.log) $(TEXDOCS:.tex=.aux) $(TEXDOCS:.tex=.toc) $(XMLMSGROOT)

distclean: clean
	@echo Full clean...
	$(RM) $(TEXDVIS) $(TEXDOCS:.tex=.toc) $(TEXPDFS)

release: pdf
	@echo Releasing all pdfs
	$(MV) $(TEXPDFS) $(RELEASEDIR)
