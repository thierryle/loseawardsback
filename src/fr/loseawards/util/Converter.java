package fr.loseawards.util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Blob;

import fr.loseawards.archive.dto.ArchiveDTO;
import fr.loseawards.archiveaward.dto.ArchiveAwardDTO;
import fr.loseawards.archivecategory.dto.ArchiveCategoryDTO;
import fr.loseawards.archivereport.dto.ArchiveReportDTO;
import fr.loseawards.archiveuser.dto.ArchiveUserDTO;
import fr.loseawards.avatar.dto.AvatarDTO;
import fr.loseawards.category.dto.CategoryDTO;
import fr.loseawards.comment.dto.CommentDTO;
import fr.loseawards.decision.dto.DecisionDTO;
import fr.loseawards.global.dto.GlobalDTO;
import fr.loseawards.image.dto.ImageDTO;
import fr.loseawards.model.Archive;
import fr.loseawards.model.ArchiveAward;
import fr.loseawards.model.ArchiveCategory;
import fr.loseawards.model.ArchiveRank;
import fr.loseawards.model.ArchiveReport;
import fr.loseawards.model.ArchiveUser;
import fr.loseawards.model.Avatar;
import fr.loseawards.model.Category;
import fr.loseawards.model.Comment;
import fr.loseawards.model.Decision;
import fr.loseawards.model.Global;
import fr.loseawards.model.Image;
import fr.loseawards.model.Nomination;
import fr.loseawards.model.User;
import fr.loseawards.model.Vote;
import fr.loseawards.nomination.dto.NominationDTO;
import fr.loseawards.user.dto.UserDTO;
import fr.loseawards.vote.dto.VoteDTO;

public class Converter {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	// ===== User =====
	
	public static UserDTO toDTO(final User user) {
		return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getAvatarId());
	}
	
	public static List<UserDTO> toUsersDTO(final List<User> users) {
		List<UserDTO> usersDTO = new ArrayList<UserDTO>();
		for (User user: users) {
			usersDTO.add(toDTO(user));
		}
		return usersDTO;
	}
	
	public static User fromDTO(final UserDTO userDTO) {
		User user = new User();
		user.setId(userDTO.getId());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setAvatarId(userDTO.getAvatarId());
		return user;
	}
	
	// ===== Nomination =====
	
	public static NominationDTO toDTO(final Nomination nomination) {
		return new NominationDTO(nomination.getId(), nomination.getUsersIds(), nomination.getCategoryId(), nomination.getReason(), nomination.getDate(), nomination.getImageId());
	}
	
	public static List<NominationDTO> toNominationsDTO(final List<Nomination> nominations) {
		List<NominationDTO> nominationsDTO = new ArrayList<NominationDTO>();
		for (Nomination nomination : nominations) {
			nominationsDTO.add(toDTO(nomination));
		}
		return nominationsDTO;
	}
	
	public static Nomination fromDTO(final NominationDTO nominationDTO) {
		Nomination nomination = new Nomination();
		nomination.setId(nominationDTO.getId());
		if (nominationDTO.getUsersIds() != null) {
			nomination.setUsersIds(nominationDTO.getUsersIds().toArray(new Long[nominationDTO.getUsersIds().size()]));
		}
		nomination.setCategoryId(nominationDTO.getCategoryId());
		nomination.setReason(nominationDTO.getReason());
		nomination.setDate(nominationDTO.getDate());
		nomination.setImageId(nominationDTO.getImageId());		
		return nomination;
	}
	
	// ===== Category =====
	
	public static CategoryDTO toDTO(final Category category) {
		return new CategoryDTO(category.getId(), category.getName());
	}
	
	public static List<CategoryDTO> toCategoriesDTO(final List<Category> categories) {
		List<CategoryDTO> categoriesDTO = new ArrayList<CategoryDTO>();
		for (Category category : categories) {
			categoriesDTO.add(toDTO(category));
		}
		return categoriesDTO;
	}
	
	public static Category fromDTO(final CategoryDTO categoryDTO) {
		Category category = new Category();
		category.setId(categoryDTO.getId());
		category.setName(categoryDTO.getName());
		return category;
	}
	
	// ===== Comment =====
	
	public static CommentDTO toDTO(final Comment comment) {
		return new CommentDTO(comment.getId(), comment.getAuthorId(), dateToString(comment.getDate()), comment.getContent(), comment.getNominationId());
	}
	
	public static List<CommentDTO> toCommentsDTO(final List<Comment> comments) {
		List<CommentDTO> commentsDTO = new ArrayList<CommentDTO>();
		for (Comment comment : comments) {
			commentsDTO.add(toDTO(comment));
		}
		return commentsDTO;
	}
	
	public static Comment fromDTO(final CommentDTO commentDTO) {
		Comment comment = new Comment();
		comment.setId(commentDTO.getId());
		comment.setAuthorId(commentDTO.getAuthorId());
		if (commentDTO.getDate() != null && !commentDTO.getDate().isEmpty()) {
			comment.setDate(stringToDate(commentDTO.getDate()));
		}
		comment.setContent(commentDTO.getContent());
		comment.setNominationId(commentDTO.getNominationId());
		return comment;
	}
	
	// ===== Avatar =====
	
	public static AvatarDTO toDTO(final Avatar avatar) {
		return new AvatarDTO(avatar.getId(), avatar.getImage() != null ? avatar.getImage().getBytes() : null);
	}
	
	public static List<AvatarDTO> toAvatarsDTO(List<Avatar> avatars) {
		List<AvatarDTO> avatarsDTO = new ArrayList<AvatarDTO>();
		for (Avatar avatar : avatars) {
			avatarsDTO.add(Converter.toDTO(avatar));
		}
		return avatarsDTO;
	}
	
	// ===== Image =====
	
	public static ImageDTO toDTO(final Image image) {
		return new ImageDTO(image.getId(), image.getImage() != null ? image.getImage().getBytes() : null);
	}
	
	public static List<ImageDTO> toImagesDTO(List<Image> images) {
		List<ImageDTO> imagesDTO = new ArrayList<ImageDTO>();
		for (Image image : images) {
			imagesDTO.add(Converter.toDTO(image));
		}
		return imagesDTO;
	}
	
	// ===== Vote =====
	
	public static Vote fromDTO(final VoteDTO voteDTO) {
		Vote vote = new Vote();
		vote.setId(voteDTO.getId());
		vote.setCategoryId(voteDTO.getCategoryId());
		vote.setNominationId(voteDTO.getNominationId());
		vote.setReason(voteDTO.getReason());
		vote.setVoterId(voteDTO.getVoterId());
		return vote;
	}
	
	public static VoteDTO toDTO(final Vote vote) {
		return new VoteDTO(vote.getId(), vote.getCategoryId(), vote.getVoterId(), vote.getNominationId(), vote.getReason());
	}
	
	public static List<VoteDTO> toVotesDTO(List<Vote> votes) {
		List<VoteDTO> votesDTO = new ArrayList<VoteDTO>();
		for (Vote vote : votes) {
			votesDTO.add(Converter.toDTO(vote));
		}
		return votesDTO;
	}
	
	// ===== Decision =====
	
	public static DecisionDTO toDTO(final Decision decision) {
		return new DecisionDTO(decision.getId(), decision.getCategoryId(), decision.getNominatedId());
	}
	
	public static List<DecisionDTO> toDecisionsDTO(final List<Decision> decisions) {
		List<DecisionDTO> decisionsDTO = new ArrayList<DecisionDTO>();
		for (Decision decision : decisions) {
			decisionsDTO.add(Converter.toDTO(decision));
		}
		return decisionsDTO;
	}
	
	public static Decision fromDTO(final DecisionDTO decisionDTO) {
		Decision decision = new Decision();
		decision.setId(decisionDTO.getId());
		decision.setCategoryId(decisionDTO.getCategoryId());
		decision.setNominatedId(decisionDTO.getNominatedId());
		return decision;
	}
	
	// ===== Global =====
	
	public static GlobalDTO toDTO(final Global global) {
		return new GlobalDTO(global.getId(), global.getKey(), global.getValue(), global.getValuesIds());
	}
	
	public static List<GlobalDTO> toGlobalsDTO(final List<Global> globals) {
		List<GlobalDTO> globalsDTO = new ArrayList<GlobalDTO>();
		for (Global global: globals) {
			globalsDTO.add(toDTO(global));
		}
		return globalsDTO;
	}
	
	public static Global fromDTO(final GlobalDTO globalDTO) {
		Global global = new Global();
		global.setId(globalDTO.getId());
		global.setKey(globalDTO.getKey());
		global.setValue(globalDTO.getValue());
		if (globalDTO.getValuesIds() != null) {
			global.setValuesIds(globalDTO.getValuesIds().toArray(new Long[globalDTO.getValuesIds().size()]));
		}	
		return global;
	}
	
	// ===== ArchiveUser =====
	
	public static ArchiveUserDTO toDTO(final ArchiveUser user) {
		return new ArchiveUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getFirstYear(), user.getLastYear());
	}
	
	public static List<ArchiveUserDTO> toArchiveUsersDTO(final List<ArchiveUser> archiveUsers) {
		List<ArchiveUserDTO> archiveUsersDTO = new ArrayList<ArchiveUserDTO>();
		for (ArchiveUser archiveUser: archiveUsers) {
			archiveUsersDTO.add(toDTO(archiveUser));
		}
		return archiveUsersDTO;
	}
	
	public static ArchiveUser fromDTO(final ArchiveUserDTO archiveUserDTO) {
		ArchiveUser archiveUser = new ArchiveUser();
		archiveUser.setId(archiveUserDTO.getId());
		archiveUser.setFirstName(archiveUserDTO.getFirstName());
		archiveUser.setLastName(archiveUserDTO.getLastName());
		archiveUser.setFirstYear(archiveUserDTO.getFirstYear());
		archiveUser.setLastYear(archiveUserDTO.getLastYear());
		return archiveUser;
	}
	
	// ===== ArchiveCategory =====
	
	public static ArchiveCategoryDTO toDTO(final ArchiveCategory archiveCategory) {
		return new ArchiveCategoryDTO(archiveCategory.getId(), archiveCategory.getName());
	}
	
	public static List<ArchiveCategoryDTO> toArchiveCategoriesDTO(final List<ArchiveCategory> archiveCategories) {
		List<ArchiveCategoryDTO> archiveCategoriesDTO = new ArrayList<ArchiveCategoryDTO>();
		for (ArchiveCategory archiveCategory: archiveCategories) {
			archiveCategoriesDTO.add(toDTO(archiveCategory));
		}
		return archiveCategoriesDTO;
	}
	
	public static ArchiveCategory fromDTO(final ArchiveCategoryDTO archiveCategoryDTO) {
		ArchiveCategory archiveCategory = new ArchiveCategory();
		archiveCategory.setId(archiveCategoryDTO.getId());
		archiveCategory.setName(archiveCategoryDTO.getName());
		return archiveCategory;
	}
	
	// ===== Archive =====
	
	public static ArchiveDTO toDTO(final Archive archive, final List<ArchiveRank> archiveRanks) {
		Map<Integer, List<Long>> ranking = new HashMap<Integer, List<Long>>();
		if (archiveRanks != null) {
			for (ArchiveRank archiveRank : archiveRanks) {
				ranking.put(archiveRank.getPosition(), Arrays.asList(archiveRank.getUsersIds()));
			}
		}
		
		return new ArchiveDTO(archive.getId(), archive.getYear(), archive.getCategoriesIds(), ranking);
	}
	
	public static List<ArchiveDTO> toArchivesDTO(List<Archive> archives, List<ArchiveRank> archiveRanks) {
		// Conversion en DTO
		List<ArchiveDTO> archivesDTO = new ArrayList<ArchiveDTO>();
		for (Archive archive : archives) {
			archivesDTO.add(Converter.toDTO(archive, getArchiveRanksOfYear(archive.getYear(), archiveRanks)));
		}
		return archivesDTO;
	}
	
	public static List<ArchiveRank> getArchiveRanksOfYear(Integer year, final List<ArchiveRank> archiveRanks) {
		if (year == null) {
			return null;
		}
		return Util.getSublistByProperty(archiveRanks, "year", year);
	}
	
	public static Archive fromDTO(final ArchiveDTO archiveDTO) {
		Archive archive = new Archive();
		archive.setId(archiveDTO.getId());
		archive.setYear(archiveDTO.getYear());
		if (archiveDTO.getCategoriesIds() != null) {
			archive.setCategoriesIds(archiveDTO.getCategoriesIds().toArray(new Long[archiveDTO.getCategoriesIds().size()]));
		}
		return archive;
	}
	
	// ===== ArchiveRank =====
	
	public static List<ArchiveRank> fromDTO(final Map<Integer, List<Long>> ranking, final Integer year) {
		if (ranking != null) {
			List<ArchiveRank> archiveRanks = new ArrayList<ArchiveRank>();
			for (Integer rank : ranking.keySet()) {
				ArchiveRank archiveRank = new ArchiveRank(null, year, rank, ranking.get(rank));
				archiveRanks.add(archiveRank);
			}
			return archiveRanks;
		}
		return null;
	}
	
	// ===== ArchiveAward =====
	
	public static ArchiveAwardDTO toDTO(final ArchiveAward archiveAward) {
		return new ArchiveAwardDTO(archiveAward.getId(), archiveAward.getYear(), archiveAward.getCategoryId(), archiveAward.getUsersIds(), archiveAward.getReason());
	}
	
	public static List<ArchiveAwardDTO> toArchiveAwardsDTO(final List<ArchiveAward> archiveAwards) {
		List<ArchiveAwardDTO> archiveAwardsDTO = new ArrayList<ArchiveAwardDTO>();
		for (ArchiveAward archiveAward : archiveAwards) {
			archiveAwardsDTO.add(toDTO(archiveAward));
		}
		return archiveAwardsDTO;
	}
	
	public static ArchiveAward fromDTO(final ArchiveAwardDTO archiveAwardDTO) {
		ArchiveAward archiveAward = new ArchiveAward();
		archiveAward.setId(archiveAwardDTO.getId());
		archiveAward.setYear(archiveAwardDTO.getYear());
		archiveAward.setCategoryId(archiveAwardDTO.getCategoryId());
		if (archiveAwardDTO.getUsersIds() != null) {
			archiveAward.setUsersIds(archiveAwardDTO.getUsersIds().toArray(new Long[archiveAwardDTO.getUsersIds().size()]));
		}
		archiveAward.setReason(archiveAwardDTO.getReason());
		return archiveAward;
	}

	// ===== ArchiveReport =====
	
	public static ArchiveReportDTO toDTO(final ArchiveReport archiveReport) {
		return new ArchiveReportDTO(archiveReport.getId(), archiveReport.getYear(), archiveReport.getReport());
	}

	public static List<ArchiveReportDTO> toArchiveReportsDTO(final List<ArchiveReport> archiveReports) {
		List<ArchiveReportDTO> archiveReportsDTO = new ArrayList<ArchiveReportDTO>();
		for (ArchiveReport archiveReport : archiveReports) {
			archiveReportsDTO.add(toDTO(archiveReport));
		}
		return archiveReportsDTO;
	}
	
	public static ArchiveReport fromDTO(final ArchiveReportDTO archiveReportDTO) {
		ArchiveReport archiveReport = new ArchiveReport();
		archiveReport.setId(archiveReportDTO.getId());
		archiveReport.setYear(archiveReportDTO.getYear());
		archiveReport.setReport(stringToBlob(archiveReportDTO.getReport()));
		return archiveReport;
	}
	
	// ===== Blob =====
	
	public static String blobToString(final Blob blob) {
		try {
			return new String(blob.getBytes(), "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Erreur dans la conversion", e);
		}
	}
	
	public static Blob stringToBlob(final String string) {
		try {
			return new Blob(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Erreur dans la conversion", e);
		}
	}
	
	// ===== Date =====
	
	public static String dateToString(final Date date) {
		return dateFormat.format(date);
	}
	
	public static Date stringToDate(final String string) {
		try {
			return dateFormat.parse(string);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Mauvais format de date", e);
		}
	}
}
