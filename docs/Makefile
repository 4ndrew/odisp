# Makefile for all documents
#
# $Id: Makefile,v 1.13 2003/12/08 22:34:46 valeks Exp $

# folders & files
TEXDOCS=impl.tex \
			gui_imp.tex \
			tz.tex \
			style-9.tex \
			odisp-style.tex
TEXDVIS=$(TEXDOCS:.tex=.dvi)
TEXPDFS=$(TEXDOCS:.tex=.pdf)
TEXHTMLS=$(TEXDOCS:.tex=.html)
TOOLS=../tools

# tools
TEXPROC=latex
PDFPROC=dvipdfm
HTMLPROC=latex2html
FIND=find
RM=rm -rf

all: tex pdf html

tex: $(TEXDVIS)

pdf: $(TEXPDFS)

htm: $(TEXHTMLS)
	$(HTMLPROC) -rootdir html/ $>

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
