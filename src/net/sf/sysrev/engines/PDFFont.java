package net.sf.sysrev.engines;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.encoding.Encoding;
import org.apache.pdfbox.pdmodel.common.PDMatrix;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class PDFFont implements Font
{
	private PDFont font;

	public PDFFont(PDFont font)
	{
		this.font = font;
	}

	public void drawString(String string, Graphics g, float fontSize, AffineTransform at, float x,
					float y) throws IOException
	{
		font.drawString(string, g, fontSize, at, x, y);
	}

	public String encode(byte[] c, int offset, int length) throws IOException
	{
		return font.encode(c, offset, length);
	}

	public boolean equals(Object other)
	{
		return font.equals(other);
	}

	public float getAverageFontWidth() throws IOException
	{
		return font.getAverageFontWidth();
	}

	public String getBaseFont()
	{
		return font.getBaseFont();
	}

	public COSBase getCOSObject()
	{
		return font.getCOSObject();
	}

	public Encoding getEncoding() throws IOException
	{
		return font.getEncoding();
	}

	public int getFirstChar()
	{
		return font.getFirstChar();
	}

	public PDRectangle getFontBoundingBox() throws IOException
	{
		return font.getFontBoundingBox();
	}

	public float getFontHeight(byte[] c, int offset, int length) throws IOException
	{
		return font.getFontHeight(c, offset, length);
	}

	public PDMatrix getFontMatrix()
	{
		return font.getFontMatrix();
	}

	public float getFontWidth(byte[] c, int offset, int length) throws IOException
	{
		return font.getFontWidth(c, offset, length);
	}

	public int getLastChar()
	{
		return font.getLastChar();
	}

	public float getStringWidth(String string) throws IOException
	{
		return font.getStringWidth(string);
	}

	public String getSubType()
	{
		return font.getSubType();
	}

	public String getType()
	{
		return font.getType();
	}

	@SuppressWarnings("unchecked")
	public List<Float> getWidths()
	{
		return (List<Float>) font.getWidths();
	}

	public int hashCode()
	{
		return font.hashCode();
	}

	public void setBaseFont(String baseFont)
	{
		font.setBaseFont(baseFont);
	}

	public void setEncoding(Encoding enc)
	{
		font.setEncoding(enc);
	}

	public void setFirstChar(int firstChar)
	{
		font.setFirstChar(firstChar);
	}

	public void setLastChar(int lastChar)
	{
		font.setLastChar(lastChar);
	}

	public void setWidths(List<Float> widths)
	{
		font.setWidths(widths);
	}

	public String toString()
	{
		return font.toString();
	}
}


