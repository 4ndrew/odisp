# Makefile for all documents
#
# $Id: Makefile,v 1.11 2003/12/08 19:59:26 valeks Exp $

# folders & files
TEXDOCS=impl.tex \
			gui_imp.tex \
			tz.tex \
			style-9.tex \
			odisp-style.tex
TEXDVIS=$(TEXDOCS:.tex=.dvi)
TOOLS=../tools

# tools
TEXPROC=latex
FIND=find
RM=rm -rf

all: texdocs xmldocs 

texdocs: $(TEXDVIS)


$(TEXDOCS:.tex=.dvi): $(@:.dvi=.tex)
	@echo Generating TeX docs...
	$(TEXPROC) $>
	$(TEXPROC) $>

clean:
	@echo Cleaning...
	$(RM) $(TEXDOCS:.tex=.log) $(TEXDOCS:.tex=.aux) $(TEXDOCS:.tex=.toc) $(XMLMSGROOT)

distclean: clean
	@echo Full clean...
	$(RM) $(TEXDOCS:.tex=.dvi) $(TEXDOCS:.tex=.toc)
