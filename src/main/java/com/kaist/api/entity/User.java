package com.kaist.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(length = 30, nullable = false)
	private String userId;

	@Column(length = 15, nullable = false)
	private String userName;

	@Column(length = 100, nullable = true)
	private String uPassword;

	@Column(length = 10, nullable = true)
	private String status;

	@Column(length = 30, nullable = true)
	private String email;

	@Column(length = 100, nullable = true)
	private String token;

	@CreationTimestamp // 시간 자동입력
	private Timestamp crateDate;

    
}
