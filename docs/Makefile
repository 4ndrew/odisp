# Makefile for TeX-documents
#

TEXDOCS=impl.tex \
        tz.tex

TEXDVIS=impl.dvi \
        tz.dvi

TEXPROC=latex

RM=rm -rf

all: pre-compile $(TEXDVIS)

pre-compile:
	@echo Start LaTeX.

impl.dvi: impl.tex
	@echo "Compiling $<..."
	$(TEXPROC) impl.tex
    
    
tz.dvi: tz.tex
	@echo "Compiling $<..."
	$(TEXPROC) tz.tex

clean:
	@echo Cleaning...
	@$(RM) *.log *.aux *.toc

distclean: clean
	@echo Full clean...
	@$(RM) *.dvi *.toc
