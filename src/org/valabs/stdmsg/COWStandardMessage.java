/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.valabs.stdmsg;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;

/** ���������� Copy-On-Write ��������� ��� ���������.
 * @author <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
 * @version $Id: COWStandardMessage.java,v 1.5 2006/03/20 14:22:58 valeks Exp $
 */
public class COWStandardMessage implements Message, Serializable, Cloneable {
    static final long serialVersionUID = 1L;
	private Message orig;
	private Message clone = null;

	public COWStandardMessage(final Message _orig) {
		orig = _orig;
	}

	/**
	 * @see org.valabs.odisp.common.Message#cloneMessage()
	 */
	public Message cloneMessage() {
		return this;
	}

	private Message messageToGet() {
		Message result = orig;
		if (clone != null) {
			result = clone;
		}
		return result;
	}
	
	private Message messageToSet() {
		if (clone == null) {
			clone = orig.cloneMessage();
		}
		return clone;
	}
	
	/**
	 * @see org.valabs.odisp.common.Message#getField(java.lang.String)
	 */
	public Object getField(String name) {
		return messageToGet().getField(name);
	}

	/**
	 * @see org.valabs.odisp.common.Message#getFieldsCount()
	 */
	public int getFieldsCount() {
		return messageToGet().getFieldsCount();
	}

	/**
	 * @see org.valabs.odisp.common.Message#getId()
	 */
	public UUID getId() {
		return messageToGet().getId();
	}

	/**
	 * @see org.valabs.odisp.common.Message#setId(UUID)
	 */
	public void setId(UUID newId) {
		messageToSet().setId(newId);
	}

	/**
	 * @see org.valabs.odisp.common.Message#getReplyTo()
	 */
	public UUID getReplyTo() {
		return messageToGet().getReplyTo();
	}

	/**
	 * @see org.valabs.odisp.common.Message#getAction()
	 */
	public String getAction() {
		return messageToGet().getAction();
	}

	/**
	 * @see org.valabs.odisp.common.Message#getDestination()
	 */
	public String getDestination() {
		return messageToGet().getDestination();
	}

	/**
	 * @see org.valabs.odisp.common.Message#setDestination(java.lang.String)
	 */
	public void setDestination(String newDest) {
		messageToSet().setDestination(newDest);
	}

	/**
	 * @see org.valabs.odisp.common.Message#setReplyTo(UUID)
	 */
	public void setReplyTo(UUID nrpt) {
		messageToSet().setReplyTo(nrpt);
	}

	/**
	 * @see org.valabs.odisp.common.Message#setAction(java.lang.String)
	 */
	public void setAction(String newAction) {
		messageToSet().setAction(newAction);
	}

	/**
	 * @see org.valabs.odisp.common.Message#getOrigin()
	 */
	public String getOrigin() {
		return messageToGet().getOrigin();
	}

	/**
	 * @see org.valabs.odisp.common.Message#setOrigin(java.lang.String)
	 */
	public void setOrigin(String newOrigin) {
		messageToSet().setOrigin(newOrigin);
	}

	/**
	 * @see org.valabs.odisp.common.Message#isCorrect()
	 */
	public boolean isCorrect() {
		return messageToGet().isCorrect();
	}

	/**
	 * @see org.valabs.odisp.common.Message#setCorrect(boolean)
	 */
	public void setCorrect(boolean newCorrectFlag) {
		messageToSet().setCorrect(newCorrectFlag);
	}

	/**
	 * @see org.valabs.odisp.common.Message#toString(boolean)
	 */
	public String toString(boolean willStackTrace) {
		return messageToGet().toString(willStackTrace);
	}

	/**
	 * @see org.valabs.odisp.common.Message#isRoutable()
	 */
	public boolean isRoutable() {
		return messageToGet().isRoutable();
	}

	/**
	 * @see org.valabs.odisp.common.Message#setRoutable(boolean)
	 */
	public void setRoutable(boolean newRoutable) {
		messageToSet().setRoutable(newRoutable);
	}

	/**
	 * @see org.valabs.odisp.common.Message#getContents()
	 */
	public Map getContents() {
		return messageToGet().getContents();
	}

	/**
	 * @see org.valabs.odisp.common.Message#addField(java.lang.String, java.lang.Object)
	 */
	public void addField(String name, Object value) {
		messageToSet().addField(name, value);
	}

	/**
	 * @see org.valabs.odisp.common.Message#getEnvelope()
	 */
	public List getEnvelope() {
		return messageToGet().getEnvelope();
	}

	/**
	 * @see org.valabs.odisp.common.Message#addToEnvelope(org.valabs.odisp.common.Message)
	 */
	public void addToEnvelope(Message envelopeMessage) {
		messageToSet().addToEnvelope(envelopeMessage);
	}

	/**
	 * @see org.valabs.odisp.common.Message#isOOB()
	 */
	public boolean isOOB() {
		return messageToGet().isOOB();
	}

	/**
	 * @see org.valabs.odisp.common.Message#setOOB(boolean)
	 */
	public void setOOB(boolean newValue) {
		messageToSet().setOOB(newValue);
	}

	public String toString() {
		return messageToGet().toString();
	}
}
