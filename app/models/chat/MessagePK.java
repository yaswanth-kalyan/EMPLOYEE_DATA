package models.chat;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Embeddable
public class MessagePK implements Serializable{

		private static final long serialVersionUID = 1L;
		
		// @Id
		 @GeneratedValue(strategy = GenerationType.SEQUENCE)
		 public Long id;
		 
		 public String randomId;


		public String getRandomId() {
			return randomId;
		}

		public void setRandomId(String randomId) {
			this.randomId = randomId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((randomId == null) ? 0 : randomId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MessagePK other = (MessagePK) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (randomId == null) {
				if (other.randomId != null)
					return false;
			} else if (!randomId.equals(other.randomId))
				return false;
			return true;
		}

}
