# Makefile for all documents
#
# $Id: Makefile,v 1.12 2003/12/08 21:53:20 valeks Exp $

# folders & files
TEXDOCS=impl.tex \
			gui_imp.tex \
			tz.tex \
			style-9.tex \
			odisp-style.tex
TEXDVIS=$(TEXDOCS:.tex=.dvi)
TEXPDFS=$(TEXDOCS:.tex=.pdf)
TOOLS=../tools

# tools
TEXPROC=latex
PDFPROC=dvipdfm
FIND=find
RM=rm -rf

all: texdocs

texdocs: $(TEXDVIS)

pdfdocs: $(TEXPDFS)

${TEXPDFS}: ${TEXDVIS}
	dvipdfm ${.TARGET:.pdf=}

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
